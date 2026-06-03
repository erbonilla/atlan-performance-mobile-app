import SwiftUI
import Shared

/// Simple route state for the SwiftUI app. Holds the NavigationStack path, the selected language
/// (chosen before account creation on the root Language Selection view), and the two modal sheets.
///
/// Non-sensitive preferences (language, haptics, keep-awake, tutorial-seen) persist across launches
/// via `UserDefaults` — a platform storage adapter (CLAUDE.md assigns storage to the platform layer).
/// TODO(persistence): move to a shared `PreferencesRepository` once the real local-storage layer
/// lands; encrypted storage remains a separate TODO for any sensitive data.
@MainActor
final class AppCoordinator: ObservableObject {
    private enum Keys {
        static let language = "pref.language"
        static let haptics = "pref.haptics"
        static let keepAwake = "pref.keepAwake"
        static let tutorialSeen = "pref.wetTutorialSeen"
        static let restSeconds = "pref.restSeconds"
    }

    private let defaults = UserDefaults.standard

    @Published var path: [AppRoute] = []

    @Published var language: Language = .en {
        didSet { defaults.set(language == .es ? "es" : "en", forKey: Keys.language) }
    }

    @Published var hapticsEnabled = true {
        didSet { defaults.set(hapticsEnabled, forKey: Keys.haptics) }
    }
    @Published var keepScreenAwake = true {
        didSet { defaults.set(keepScreenAwake, forKey: Keys.keepAwake) }
    }
    /// Rest window between sets (seconds), surfaced in Settings; feeds the timer machine.
    @Published var restSeconds = 30 {
        didSet { defaults.set(restSeconds, forKey: Keys.restSeconds) }
    }

    /// One-time Wet Mode gesture coach-mark flag — persisted so it shows once per install.
    var wetModeTutorialSeen = false {
        didSet { defaults.set(wetModeTutorialSeen, forKey: Keys.tutorialSeen) }
    }

    // Transient: true when Wet Mode should rebuild from a saved snapshot (resume) vs start fresh.
    var wetResume = false

    // Modal sheets.
    @Published var whyConceptKey: String? = nil
    @Published var swapperSessionId: String? = nil

    init() {
        // Restore persisted preferences (booleans default to true when never set).
        if defaults.string(forKey: Keys.language) == "es" { language = .es }
        if defaults.object(forKey: Keys.haptics) != nil { hapticsEnabled = defaults.bool(forKey: Keys.haptics) }
        if defaults.object(forKey: Keys.keepAwake) != nil { keepScreenAwake = defaults.bool(forKey: Keys.keepAwake) }
        if defaults.object(forKey: Keys.restSeconds) != nil { restSeconds = defaults.integer(forKey: Keys.restSeconds) }
        wetModeTutorialSeen = defaults.bool(forKey: Keys.tutorialSeen)
    }

    func selectLanguage(_ language: Language) {
        self.language = language
        path = [.welcome]
    }

    func go(_ route: AppRoute) { path.append(route) }
    func popToRoot() { path.removeAll() }
    func pop() { _ = path.popLast() }

    func presentWhy(_ conceptKey: String) { whyConceptKey = conceptKey }
    func presentSwapper(_ sessionId: String) { swapperSessionId = sessionId }
}
