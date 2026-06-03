package com.atlan.performance.shared.localization

import com.atlan.performance.shared.localization.LocalizedStringKey.*

/**
 * First-class Spanish copy — emotional parity, not literal translation. Equal priority to English;
 * never an optional fallback. Long strings wrap on screen; they are never shrunk to fit.
 */
internal object SpanishCopy {
    val values: Map<LocalizedStringKey, String> = mapOf(
        LANGUAGE_CHOOSE_ENGLISH to "English",
        LANGUAGE_CHOOSE_SPANISH to "Español",
        ONBOARDING_WELCOME_EYEBROW to "Bienvenida",
        ONBOARDING_WELCOME_TITLE to "Hecho para quienes entrenan entre todo lo demás.",
        ONBOARDING_WELCOME_BODY to "Atlan adapta el plan a tu semana — no al revés. " +
            "Antes de empezar, unas preguntas cortas para afinarla a tu manera de entrenar.",
        ONBOARDING_WELCOME_CTA to "Comenzar",
        CALIBRATION_TITLE to "No hay respuestas correctas. Solo calibración.",
        CALIBRATION_SUBTITLE to "Unas preguntas cortas",
        TUNED_TITLE to "Así voy a trabajar contigo.",
        TUNED_CTA to "Ver tu primera sesión",
        DASHBOARD_TODAY_LABEL to "Hoy · Piscina",
        DASHBOARD_START_SESSION to "Empezar sesión",
        SESSION_WHY_THRESHOLD_TITLE to "Por qué umbral",
        WET_MODE_OFFLINE_CACHED to "Sin conexión · En caché",
        WET_MODE_PAUSE to "Pausar",
        WET_MODE_COMPLETE to "Completar",
        SWAPPER_ACCEPT to "Aceptar cambio",
        SWAPPER_SKIP_TODAY to "Saltar hoy",

        SESSION_DETAIL_DATE to "Hoy · martes, 26 de mayo",

        WET_MODE_OFFLINE_ONLINE to "En línea",
        WET_MODE_OFFLINE_SYNC_PENDING to "Sincronización pendiente",
        WET_MODE_OFFLINE_SAVED_LOCALLY to "Guardado localmente",
        WET_MODE_SYNCING to "Sincronizando…",
        WET_MODE_RETRY_SYNC to "Reintentar sincronización",
        WET_MODE_SYNC_SAVED_OFFLINE to "Guardado sin conexión. Sincronizaremos cuando vuelvas a tener conexión.",
        WET_MODE_SYNC_FAILED to "La sincronización falló — tus resultados están a salvo. Reintenta cuando vuelvas a tener conexión.",

        WET_MODE_TUTORIAL_TITLE to "Cómo funciona",
        WET_MODE_TUTORIAL_COMPLETE to "Desliza a la derecha o toca para Completar",
        WET_MODE_TUTORIAL_PAUSE to "Desliza a la izquierda o toca para Pausar",
        WET_MODE_TUTORIAL_EXIT to "Desliza hacia abajo para Salir",
        WET_MODE_TUTORIAL_GOT_IT to "Entendido",

        WET_MODE_REST to "Descanso",
        WET_MODE_NEXT to "Siguiente",
        WET_MODE_PAUSED to "En pausa",
        WET_MODE_OVER_TARGET to "Sobre el objetivo",
        WET_MODE_OVER_TARGET_PACE to "Sobre el ritmo objetivo",
        WET_MODE_TARGET_PACE to "Ritmo objetivo %1\$s",
        WET_MODE_SET to "Serie %1\$s",
        WET_MODE_SET_COMPLETE to "Serie %1\$s completada",

        WET_MODE_RESUME to "Reanudar",
        WET_MODE_END to "Terminar",
        WET_MODE_END_SESSION to "Terminar sesión",
        WET_MODE_SKIP_REST to "Saltar descanso",
        WET_MODE_SWIPE_LEFT_HINT to "← Desliza a la izquierda",
        WET_MODE_SWIPE_RIGHT_HINT to "Desliza a la derecha →",
        WET_MODE_LABEL_PAUSE to "Pausar serie. Desliza a la izquierda o activa.",
        WET_MODE_LABEL_RESUME to "Reanudar serie. Desliza a la izquierda o activa.",
        WET_MODE_LABEL_COMPLETE to "Completar serie. Desliza a la derecha o activa.",
        WET_MODE_LABEL_SKIP_REST to "Saltar descanso. Desliza a la derecha o activa.",
        WET_MODE_ACTION_COMPLETE to "Completar serie",
        WET_MODE_ACTION_PAUSE to "Pausar serie",
        WET_MODE_ACTION_EXIT to "Salir del Modo Mojado",

        WET_MODE_SUMMARY_COMPLETE to "Entrenamiento completo",
        WET_MODE_SUMMARY_ENDED to "Sesión terminada",
        WET_MODE_SUMMARY_SETS to "%1\$s de %2\$s series",
        WET_MODE_DONE to "Listo",

        WET_MODE_EARLY_TITLE to "¿Completar la serie antes de tiempo?",
        WET_MODE_EARLY_MESSAGE to "Esto marca %1\$s como completada con %2\$s restantes.",
        WET_MODE_EARLY_CONFIRM to "Completar serie",
        WET_MODE_KEEP_GOING to "Seguir",
        WET_MODE_EXIT_TITLE to "¿Terminar esta sesión?",
        WET_MODE_EXIT_MESSAGE to "Tus series completadas están guardadas. El resto de la sesión no se registrará.",

        WET_MODE_ANNOUNCE_PAUSED to "Serie en pausa",
        WET_MODE_ANNOUNCE_SET_STARTED to "%1\$s iniciada",
        WET_MODE_ANNOUNCE_SET_COMPLETE_REST to "Serie %1\$s completada. Descanso."
    )
}
