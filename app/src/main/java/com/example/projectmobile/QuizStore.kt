package com.example.projectmobile

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class QuizQuestion(val text: String, val options: List<String>, val correctIndex: Int)

data class Quiz(
    val id: String,
    val classCode: String,
    val title: String,
    val durationMin: Int,
    val passScore: Int,
    val questions: List<QuizQuestion>,
    val createdAt: Long
)

/** answers berisi indeks pilihan per soal, atau -1 bila tidak dijawab. */
data class QuizAttempt(
    val quizId: String,
    val answers: List<Int>,
    val correct: Int,
    val total: Int,
    val percent: Int,
    val finishedAt: Long
)

/**
 * Kuis kelas mandiri: dibuat pengajar, dikerjakan pelajar.
 * Disimpan di perangkat (SharedPreferences) sampai terhubung ke server.
 */
object QuizStore {

    private const val PREFS = "quiz_prefs"
    private const val KEY_QUIZZES = "quizzes"
    private const val KEY_ATTEMPTS = "attempts"

    private fun sp(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // ------------------------------------------------------------------ Kuis

    private fun readQuizzes(ctx: Context): MutableList<Quiz> {
        val raw = sp(ctx).getString(KEY_QUIZZES, null) ?: return mutableListOf()
        val out = mutableListOf<Quiz>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val qs = mutableListOf<QuizQuestion>()
                val qa = o.getJSONArray("questions")
                for (j in 0 until qa.length()) {
                    val q = qa.getJSONObject(j)
                    val opts = mutableListOf<String>()
                    val oa = q.getJSONArray("options")
                    for (k in 0 until oa.length()) opts.add(oa.getString(k))
                    qs.add(QuizQuestion(q.getString("text"), opts, q.getInt("correct")))
                }
                out.add(
                    Quiz(
                        id = o.getString("id"),
                        classCode = o.getString("classCode"),
                        title = o.getString("title"),
                        durationMin = o.optInt("durationMin", 20),
                        passScore = o.optInt("passScore", 70),
                        questions = qs,
                        createdAt = o.optLong("createdAt")
                    )
                )
            }
        } catch (e: Exception) {
            // data rusak: abaikan
        }
        return out
    }

    private fun writeQuizzes(ctx: Context, list: List<Quiz>) {
        val arr = JSONArray()
        for (z in list) {
            val qa = JSONArray()
            for (q in z.questions) {
                qa.put(
                    JSONObject().apply {
                        put("text", q.text)
                        put("options", JSONArray(q.options))
                        put("correct", q.correctIndex)
                    }
                )
            }
            arr.put(
                JSONObject().apply {
                    put("id", z.id)
                    put("classCode", z.classCode)
                    put("title", z.title)
                    put("durationMin", z.durationMin)
                    put("passScore", z.passScore)
                    put("questions", qa)
                    put("createdAt", z.createdAt)
                }
            )
        }
        sp(ctx).edit().putString(KEY_QUIZZES, arr.toString()).apply()
    }

    fun quizzes(ctx: Context, classCode: String): List<Quiz> =
        readQuizzes(ctx).filter { it.classCode == classCode }.sortedBy { it.createdAt }

    fun byId(ctx: Context, id: String): Quiz? = readQuizzes(ctx).firstOrNull { it.id == id }

    fun add(
        ctx: Context,
        classCode: String,
        title: String,
        durationMin: Int,
        passScore: Int,
        questions: List<QuizQuestion>
    ): Quiz {
        val list = readQuizzes(ctx)
        val now = System.currentTimeMillis()
        val z = Quiz("Q$now", classCode, title, durationMin, passScore, questions, now)
        list.add(z)
        writeQuizzes(ctx, list)
        return z
    }

    fun delete(ctx: Context, id: String) {
        writeQuizzes(ctx, readQuizzes(ctx).filter { it.id != id })
        writeAttempts(ctx, readAttempts(ctx).filter { it.quizId != id })
    }

    fun countFor(ctx: Context, classCode: String): Int = quizzes(ctx, classCode).size

    /** Kuis dari kelas yang diikuti dan belum pernah dikerjakan. */
    fun pendingCount(ctx: Context): Int =
        ClassStore.joined(ctx).sumOf { c -> quizzes(ctx, c.code).count { attempt(ctx, it.id) == null } }

    // -------------------------------------------------------------- Pengerjaan

    private fun readAttempts(ctx: Context): MutableList<QuizAttempt> {
        val raw = sp(ctx).getString(KEY_ATTEMPTS, null) ?: return mutableListOf()
        val out = mutableListOf<QuizAttempt>()
        try {
            val arr = JSONArray(raw)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val ans = mutableListOf<Int>()
                val aa = o.getJSONArray("answers")
                for (k in 0 until aa.length()) ans.add(aa.getInt(k))
                out.add(
                    QuizAttempt(
                        quizId = o.getString("quizId"),
                        answers = ans,
                        correct = o.optInt("correct"),
                        total = o.optInt("total"),
                        percent = o.optInt("percent"),
                        finishedAt = o.optLong("finishedAt")
                    )
                )
            }
        } catch (e: Exception) {
            // data rusak: abaikan
        }
        return out
    }

    private fun writeAttempts(ctx: Context, list: List<QuizAttempt>) {
        val arr = JSONArray()
        for (a in list) {
            arr.put(
                JSONObject().apply {
                    put("quizId", a.quizId)
                    put("answers", JSONArray(a.answers))
                    put("correct", a.correct)
                    put("total", a.total)
                    put("percent", a.percent)
                    put("finishedAt", a.finishedAt)
                }
            )
        }
        sp(ctx).edit().putString(KEY_ATTEMPTS, arr.toString()).apply()
    }

    /** Hasil pengerjaan terakhir (mengerjakan ulang menggantikan hasil lama). */
    fun attempt(ctx: Context, quizId: String): QuizAttempt? =
        readAttempts(ctx).firstOrNull { it.quizId == quizId }

    fun saveAttempt(ctx: Context, quiz: Quiz, answers: List<Int>): QuizAttempt {
        var correct = 0
        quiz.questions.forEachIndexed { i, q ->
            if (answers.getOrNull(i) == q.correctIndex) correct++
        }
        val total = quiz.questions.size
        val percent = if (total == 0) 0 else Math.round(correct * 100f / total)
        val a = QuizAttempt(quiz.id, answers, correct, total, percent, System.currentTimeMillis())
        val list = readAttempts(ctx).filter { it.quizId != quiz.id }.toMutableList()
        list.add(a)
        writeAttempts(ctx, list)
        return a
    }

    fun isPassed(quiz: Quiz, attempt: QuizAttempt): Boolean = attempt.percent >= quiz.passScore
}
