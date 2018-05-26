package com.marktony.zhihudaily.injection

import android.content.Context
import com.marktony.zhihudaily.data.source.local.*
import com.marktony.zhihudaily.data.source.remote.*
import com.marktony.zhihudaily.data.source.repository.*
import com.marktony.zhihudaily.database.AppDatabase
import com.marktony.zhihudaily.util.AppExecutors

object Injection {

    private val appExecutors: AppExecutors = AppExecutors()

    fun provideZhihuDailyNewsRepository(context: Context): ZhihuDailyNewsRepository = ZhihuDailyNewsRepository.getInstance(ZhihuDailyNewsRemoteDataSource.getInstance(appExecutors), ZhihuDailyNewsLocalDataSource.getInstance(appExecutors, AppDatabase.getInstance(context).zhihuDailyNewsDao()))

    fun provideZhihuDailyContentRepository(context: Context): ZhihuDailyContentRepository = ZhihuDailyContentRepository.getInstance(ZhihuDailyContentRemoteDataSource.getInstance(appExecutors), ZhihuDailyContentLocalDataSource.getInstance(appExecutors, AppDatabase.getInstance(context).zhihuDailyContentDao()))

    fun provideGuokrHandpickNewsRepository(context: Context): GuokrHandpickNewsRepository = GuokrHandpickNewsRepository.getInstance(GuokrHandpickNewsRemoteDataSource.getInstance(appExecutors), GuokrHandpickNewsLocalDataSource.getInstance(appExecutors, AppDatabase.getInstance(context).guokrHandpickNewsDao()))

    fun provideGuokrHandpickContentRepository(context: Context): GuokrHandpickContentRepository = GuokrHandpickContentRepository.getInstance(GuokrHandpickContentRemoteDataSource.getInstance(appExecutors), GuokrHandpickContentLocalDataSource.getInstance(appExecutors, AppDatabase.getInstance(context).guokrHandpickContentDao()))

    fun provideDoubanMomentNewsRepository(context: Context): DoubanMomentNewsRepository = DoubanMomentNewsRepository.getInstance(DoubanMomentNewsRemoteDataSource.getInstance(appExecutors), DoubanMomentNewsLocalDataSource.getInstance(appExecutors, AppDatabase.getInstance(context).doubanMomentNewsDao()))

    fun provideDoubanMomentContentRepository(context: Context): DoubanMomentContentRepository = DoubanMomentContentRepository.getInstance(DoubanMomentContentRemoteDataSource.getInstance(appExecutors), DoubanMomentContentLocalDataSource.getInstance(appExecutors, AppDatabase.getInstance(context).doubanMomentContentDao()))

}