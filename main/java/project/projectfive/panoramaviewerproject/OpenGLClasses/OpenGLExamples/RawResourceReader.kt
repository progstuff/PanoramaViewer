package project.projectfive.panoramaviewerproject.OpenGLClasses.OpenGLExamples

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object RawResourceReader {
    fun readTextFileFromRawResource(
        context: Context,
        resourceId: Int
    ): String {
        val inputStream: InputStream = context.getResources().openRawResource(
            resourceId
        )
        val inputStreamReader = InputStreamReader(
            inputStream
        )
        val bufferedReader = BufferedReader(
            inputStreamReader
        )
        var nextLine: String? = null
        val body = StringBuilder()
        try {
            while (bufferedReader.readLine().also({ nextLine = it }) != null) {
                body.append(nextLine)
                body.append('\n')
            }
        } catch (e: IOException) {
            return ""
        }
        return body.toString()
    }
}