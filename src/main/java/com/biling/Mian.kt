package com.biling

import com.biling.repo.models.request.Request
import com.google.gson.Gson
import java.io.File
import java.util.*

//[],[],[],[],[],[50.458711,30.616292,2,'14:33:30',52410],[50.458718,30.616305,1,'14:33:31',52411],[50.45872,30.61632,0,'14:33:32',52412],[50.458692,30.616307,3,'14:33:42',52422],[50.458688,30.616305,1,'14:33:45',52425],[50.45868,30.616305,0,'14:33:46',52426],[50.458676,30.616302,1,'14:33:47',52427],[50.458673,30.6163,1,'14:33:48',52428],[50.458671,30.6163,0,'14:33:49',52429],[50.458675,30.616303,1,'14:33:50',52430],[50.458674,30.616217,5,'14:34:07',52447],[50.458675,30.616213,1,'14:34:09',52449],
fun main() {
//    val traks = arrayListOf<RequestTrack>(
//            RequestTrack(50.458676, 30.616107, 0, "14:33:21", 52401),
//            RequestTrack(50.458696, 30.616187, 2, "14:33:25", 52405),
//            RequestTrack(50.458701, 30.616212, 2, "14:33:26", 52406),
//            RequestTrack(50.458703, 30.616231, 1, "14:33:27", 52407),
//            RequestTrack(50.458702, 30.616254, 1, "14:33:28", 52408),
//            RequestTrack(50.458704, 30.616275, 1, "14:33:29", 52409)
//    )
//    val entry = Request(ArrayList(traks))
//    println(Gson().toJson(entry))

    val string = readFile()

    val result = Gson().fromJson(string, Request::class.java)
    println(result)
}

fun readFile(): String {
    // pass the path to the file as a parameter
    val file = File("sample.txt")
    val sc = Scanner(file)
    val buffer = StringBuffer()
    while (sc.hasNextLine())
        buffer.append(sc.nextLine())
    return buffer.toString()
}
