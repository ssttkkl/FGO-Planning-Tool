package com.ssttkkl.fgoplanningtool.ui.utils

import kotlin.reflect.KClass

class NoInterfaceImplException(c: KClass<out Any>) : Exception("No interface ${c.simpleName} implementation is found. ")