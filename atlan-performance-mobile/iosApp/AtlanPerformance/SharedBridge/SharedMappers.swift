import SwiftUI
import Shared

/// Disambiguates the Kotlin `LocalizedStringKey` from SwiftUI's same-named type.
typealias CopyKey = Shared.LocalizedStringKey

/// Copy access through the shared bilingual layer (keeps EN/ES parity authoritative across platforms).
/// Kotlin `object AtlanCopy` is exposed to Swift as `AtlanCopy.shared`.
func localized(_ key: CopyKey, _ language: Language) -> String {
    AtlanCopy.shared.get(key: key, language: language)
}

/// Templated copy: resolves a `%1$s`, `%2$s`, … key and substitutes positional args in order.
/// Mirrors `AtlanCopy.format` (Kotlin vararg doesn't bridge cleanly, so we substitute Swift-side).
func localized(_ key: CopyKey, _ language: Language, _ args: String...) -> String {
    var result = AtlanCopy.shared.get(key: key, language: language)
    for (i, arg) in args.enumerated() {
        result = result.replacingOccurrences(of: "%\(i + 1)$s", with: arg)
    }
    return result
}

extension OfflineStatus {
    /// Calm, non-alarming label for the offline pill — resolved through the shared bilingual layer.
    func pillLabel(_ language: Language) -> String {
        let key: CopyKey
        switch self {
        case .online: key = .wetModeOfflineOnline
        case .syncPending: key = .wetModeOfflineSyncPending
        case .syncFailedSavedLocally: key = .wetModeOfflineSavedLocally
        default: key = .wetModeOfflineCached
        }
        return localized(key, language)
    }
}
