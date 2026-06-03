import UserNotifications

/// Local session reminders — fully client-side (no backend). Requests notification authorization and
/// schedules a calm daily reminder. Honest scope: a local notification only; no server push.
enum NotificationScheduler {
    static let reminderId = "atlan.session.reminder"
    private static let reminderHour = 7 // calm morning nudge, matching the seed cadence

    /// Request authorization, then schedule the daily reminder on grant. Calls back on the main thread.
    static func enableDailyReminder(isES: Bool, completion: @escaping (Bool) -> Void) {
        let center = UNUserNotificationCenter.current()
        center.requestAuthorization(options: [.alert, .sound]) { granted, _ in
            if granted { schedule(isES: isES) }
            DispatchQueue.main.async { completion(granted) }
        }
    }

    static func cancel() {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [reminderId])
    }

    private static func schedule(isES: Bool) {
        let content = UNMutableNotificationContent()
        content.title = isES ? "Tu sesión de hoy" : "Today's session"
        content.body = isES ? "Una sesión de umbral te espera cuando estés listo."
                            : "A Threshold session is ready when you are."
        content.sound = .default

        var components = DateComponents()
        components.hour = reminderHour
        let trigger = UNCalendarNotificationTrigger(dateMatching: components, repeats: true)
        let request = UNNotificationRequest(identifier: reminderId, content: content, trigger: trigger)
        let center = UNUserNotificationCenter.current()
        center.removePendingNotificationRequests(withIdentifiers: [reminderId])
        center.add(request)
    }
}
