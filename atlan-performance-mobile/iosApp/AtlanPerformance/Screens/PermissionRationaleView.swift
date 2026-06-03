import SwiftUI

/// Permission rationale — explains the value of an optional capability before any system prompt, and
/// never fires one cold. This milestone has no notification/health infra, so opting in only records
/// intent (a calm acknowledgement); the real OS prompt + wiring (UserNotifications, HealthKit) is a
/// TODO. Always skippable. Pushed from Settings.
struct PermissionRationaleView: View {
    @EnvironmentObject private var coordinator: AppCoordinator
    let kind: PermissionKind
    @State private var acknowledged = false

    var body: some View {
        let es = coordinator.language == .es
        let c = content(es)
        ScrollView {
            VStack(alignment: .leading, spacing: AtlanSpacing.md) {
                Text((es ? "Opcional" : "Optional").uppercased())
                    .font(AtlanTypography.label).foregroundColor(AtlanColors.tideDeep)
                Text(c.title).font(AtlanTypography.display).foregroundColor(AtlanColors.abyss)
                    .fixedSize(horizontal: false, vertical: true)
                Text(c.body).foregroundColor(AtlanColors.abyss)
                    .fixedSize(horizontal: false, vertical: true)

                VStack(alignment: .leading, spacing: AtlanSpacing.sm) {
                    ForEach(c.points, id: \.self) { point in
                        HStack(alignment: .top, spacing: AtlanSpacing.sm) {
                            Text("·").foregroundColor(AtlanColors.tide)
                            Text(point).foregroundColor(AtlanColors.abyss)
                                .fixedSize(horizontal: false, vertical: true)
                        }
                    }
                }
                .frame(maxWidth: .infinity, alignment: .leading)
                .padding(AtlanSpacing.lg)
                .background(AtlanColors.paper)
                .clipShape(RoundedRectangle(cornerRadius: AtlanRadii.lg))

                if acknowledged {
                    Text(es ? "Listo. Te pediremos confirmar el permiso cuando esté disponible."
                            : "Got it. We'll ask you to confirm the permission when it's available.")
                        .foregroundColor(AtlanColors.tideDeep)
                        .fixedSize(horizontal: false, vertical: true)
                    AtlanButton(title: es ? "Hecho" : "Done", coral: false) { coordinator.pop() }
                } else {
                    AtlanButton(title: c.cta, coral: true) { acknowledged = true }
                    Button { coordinator.pop() } label: {
                        Text(es ? "Ahora no" : "Not now")
                            .foregroundColor(AtlanColors.tideDeep)
                            .frame(maxWidth: .infinity, minHeight: 44)
                            .contentShape(Rectangle())
                    }
                    .buttonStyle(AtlanPressStyle())
                }
            }
            .padding(AtlanSpacing.xl)
        }
        .background(AtlanColors.foamWarm.ignoresSafeArea())
    }

    private struct Content { let title: String; let body: String; let points: [String]; let cta: String }

    private func content(_ es: Bool) -> Content {
        switch kind {
        case .notifications:
            return Content(
                title: es ? "Recordatorios suaves" : "Gentle reminders",
                body: es ? "Atlan puede avisarte antes de una sesión y cuando termina el descanso — con calma, nunca insistiendo."
                         : "Atlan can nudge you before a session and when rest ends — calm, never nagging.",
                points: es ? ["Aviso antes de la sesión de hoy", "Señal cuando termina el descanso", "Tú eliges la frecuencia · mínima por defecto"]
                           : ["A heads-up before today's session", "A cue when rest ends", "You choose the cadence · minimal by default"],
                cta: es ? "Activar recordatorios" : "Turn on reminders"
            )
        case .health:
            return Content(
                title: es ? "Conecta tus datos de salud" : "Connect your health data",
                body: es ? "Opcional: sincroniza tus sesiones con Apple Health para que tu entrenamiento viva junto al resto de tu salud."
                         : "Optional: sync your sessions with Apple Health so training lives alongside the rest of your health.",
                points: es ? ["Tus entrenos completados, en tu app de salud", "Tú tienes el control · nada se comparte sin permiso", "Puedes desconectarlo cuando quieras"]
                           : ["Your completed workouts, in your health app", "You stay in control · nothing shared without permission", "Disconnect anytime"],
                cta: es ? "Conectar" : "Connect"
            )
        }
    }
}
