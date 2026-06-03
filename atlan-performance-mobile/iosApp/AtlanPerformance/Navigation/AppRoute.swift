import Foundation

/// Primary navigation destinations for the iOS shell, driven through a NavigationStack path.
/// Why Modal and Session Swapper are presented as native sheets (see AppCoordinator), not routes.
enum AppRoute: Hashable {
    case welcome
    case calibration
    case tunedSummary
    case dashboard
    case sessionDetail
    case workoutPrep
    case wetMode
    case settings
}
