/*
 * Copyright (c) 2018, Omnisol Ltd. All rights reserved.
 * OMNISOL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package zakhi.advent.helpers

import java.io.InputStream


fun <T> withResourceStream(name: String, block: (InputStream) -> T): T =
    dummy::class.java.classLoader.getResourceAsStream(name).use(block)


private val dummy = object {}
