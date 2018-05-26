package com.marktony.zhihudaily.util

import kotlin.coroutines.experimental.EmptyCoroutineContext

class SingleExecutors : AppExecutors(EmptyCoroutineContext, EmptyCoroutineContext, EmptyCoroutineContext)