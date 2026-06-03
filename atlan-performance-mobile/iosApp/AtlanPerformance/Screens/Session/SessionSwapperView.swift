import SwiftUI
import Shared

/// Session Swapper — absorbs disruption without shame. Neither action is failure: Accept updates the
/// plan locally and queues sync; Skip updates locally with no red/failure copy. No warning states.
struct SessionSwapperView: View {
    @EnvironmentObject private var container: SharedContainer
    @Environment(\.dismiss) private var dismiss
    let sessionId: String
    @State private var state: SessionSwapperState?
    @State private var accepting = false

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: AtlanSpacing.md) {
                if let s = state {
                    AtlanPill(text: s.tag)
                    Text(s.empathyLine).font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)

                    swapRow("Original", s.originalLabel)
                    swapRow("Replacement", "\(s.proposal.replacementTitle) · \(s.proposal.replacementDetail)")
                    swapRow("Weekly load", s.proposal.weeklyLoadStatus)

                    Text(s.proposal.affirmation)
                        .foregroundColor(AtlanColors.tideDeep)
                        .frame(maxWidth: .infinity)

                    AtlanButton(title: "Accept swap", coral: true, isEnabled: !accepting,
                                isLoading: accepting) {
                        accepting = true
                        Task {
                            await container.acceptSwap(proposalId: s.proposal.id,
                                                       originalSessionId: s.proposal.originalSessionId)
                            dismiss()
                        }
                    }
                    Button {
                        Task { await container.skipToday(sessionId: sessionId); dismiss() }
                    } label: {
                        Text("Skip today")
                            .frame(maxWidth: .infinity, minHeight: 44)
                            .foregroundColor(AtlanColors.tideDeep)
                            .contentShape(Rectangle())
                    }
                    .buttonStyle(AtlanPressStyle())
                    .disabled(accepting)
                    .padding(.vertical, AtlanSpacing.sm)
                } else {
                    ProgressView().tint(AtlanColors.tide)
                }
            }
            .padding(AtlanSpacing.xl)
        }
        .background(AtlanColors.paper.ignoresSafeArea())
        .task { state = await container.proposeSwap(sessionId: sessionId) }
    }

    private func swapRow(_ label: String, _ value: String) -> some View {
        VStack(alignment: .leading, spacing: AtlanSpacing.xs) {
            Text(label.uppercased()).font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
            Text(value).foregroundColor(AtlanColors.abyss)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(AtlanSpacing.lg)
        .background(AtlanColors.foamWarm)
        .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))
    }
}
