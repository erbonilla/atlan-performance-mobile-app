package com.atlan.performance.shared.data.sync

/** Type of pending local write to be reconciled with the backend later. No backend exists yet. */
enum class SyncOperation {
    COMPLETE_SET,
    COMPLETE_SESSION,
    ACCEPT_SWAP,
    SKIP_SESSION,
    UPDATE_PROFILE,
    SAVE_POST_SESSION
}
