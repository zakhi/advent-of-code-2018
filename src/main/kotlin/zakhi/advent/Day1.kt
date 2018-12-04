/*
 * Copyright (c) 2018, Omnisol Ltd. All rights reserved.
 * OMNISOL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package zakhi.advent

import zakhi.advent.helpers.withResourceStream

fun main() {
    val sum = withResourceStream("day 1 input.txt") {
        it.bufferedReader().lineSequence().map(String::toInt).sum()
    }

    println(sum)
}