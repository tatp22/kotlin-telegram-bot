package com.github.kotlintelegrambot.dispatcher.handlers

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.CallbackQuery
import com.github.kotlintelegrambot.entities.Update

data class CallbackQueryHandlerEnvironment(
    val bot: Bot,
    val update: Update,
    val callbackQuery: CallbackQuery
)

class CallbackQueryHandler(
    callbackData: String? = null,
    private val callbackAnswerText: String? = null,
    private val callbackAnswerShowAlert: Boolean? = null,
    private val callbackAnswerUrl: String? = null,
    private val callbackAnswerCacheTime: Int? = null,
    private val handleCallbackQuery: HandleCallbackQuery
) : Handler {

    private val callbackDataRegex = callbackData?.let { Regex(Regex.escape(it) + "\\b.*") }

    override fun checkUpdate(update: Update): Boolean {
        val data = update.callbackQuery?.data
        return when {
            data == null -> false
            callbackDataRegex == null -> true
            else -> data.matches(callbackDataRegex)
        }
    }

    override suspend fun handleUpdate(bot: Bot, update: Update) {
        checkNotNull(update.callbackQuery)
        val callbackQueryHandlerEnv = CallbackQueryHandlerEnvironment(
            bot,
            update,
            update.callbackQuery
        )
        handleCallbackQuery(callbackQueryHandlerEnv)

        val callbackQueryId = update.callbackQuery.id
        bot.answerCallbackQuery(
            callbackQueryId = callbackQueryId,
            text = callbackAnswerText,
            showAlert = callbackAnswerShowAlert,
            url = callbackAnswerUrl,
            cacheTime = callbackAnswerCacheTime,
        )
    }
}
