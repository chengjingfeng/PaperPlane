package com.marktony.zhihudaily.timeline

import com.marktony.zhihudaily.data.ZhihuDailyNewsQuestion
import com.marktony.zhihudaily.data.source.repository.ZhihuDailyNewsRepository
import kotlinx.coroutines.experimental.Unconfined
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class ZhihuDailyNewsPresenterTest {

    @Mock
    private lateinit var zhihuDailyNewsRepository: ZhihuDailyNewsRepository

    @Mock
    private lateinit var zhihuDailyView: ZhihuDailyContract.View

    private lateinit var zhihuDailyNewsPresenter: ZhihuDailyPresenter

    private lateinit var zhihuDailyNewsStories: List<ZhihuDailyNewsQuestion>

    @Before
    fun setupZhihuDailyPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this)

        // Get a reference to the class under test
        zhihuDailyNewsPresenter = ZhihuDailyPresenter(zhihuDailyView, zhihuDailyNewsRepository, Unconfined)

        // The presenter won't update the view unless it's active.
        `when`(zhihuDailyView.isActive).thenReturn(true)

        // We start the zhihu daily news story to 3, with one favorited and two unfavorited.
        zhihuDailyNewsStories = mutableListOf(
                ZhihuDailyNewsQuestion(listOf("https:\\/\\/pic1.zhimg.com\\/v2-5c5f75baa911c58f2476334180f5cda0.jpg"), 0, 9683773, "052422", "小事 · 直到麻木了，就到了要走的时候", false, 1527333963L),
                ZhihuDailyNewsQuestion(listOf("https:\\/\\/pic4.zhimg.com\\/v2-fe3110793602fba3e4e6dea967f41f47.jpg"), 0, 9683986, "052421", "十年后再看《蓝宇》", false, 1527337920L),
                ZhihuDailyNewsQuestion(listOf("https:\\/\\/pic2.zhimg.com\\/v2-7fbd7d4bc19c79608e775cd2b4e57545.jpg"), 0, 9683959, "052419", "作为咨询师，遇见不止一次，来访者威胁说要自杀……", true, 1527340803L)
        )
    }

    @Test
    fun createPresenter_setsThePresenterToView() {
        // Get a reference to the class under test
        zhihuDailyNewsPresenter = ZhihuDailyPresenter(zhihuDailyView, zhihuDailyNewsRepository, Unconfined)

        // Then the presenter is set to the view
        verify(zhihuDailyView).mPresenter = zhihuDailyNewsPresenter
    }

}