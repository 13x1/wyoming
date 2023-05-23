package cx.lexi.wyoming.debug

val printed = mutableSetOf<String>()

fun printOnce(str: String) {
    if (printed.contains(str)) return
    println("[PrintOnce] $str")
    printed.add(str)
}