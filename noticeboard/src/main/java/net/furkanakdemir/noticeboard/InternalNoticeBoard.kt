package net.furkanakdemir.noticeboard

import android.content.Context
import net.furkanakdemir.noticeboard.config.ConfigRepository
import net.furkanakdemir.noticeboard.config.NoticeBoardConfigRepository
import net.furkanakdemir.noticeboard.data.datasource.NoticeBoardDataSourceFactory
import net.furkanakdemir.noticeboard.data.mapper.ChangeDomainMapper
import net.furkanakdemir.noticeboard.data.mapper.ReleaseDomainMapper
import net.furkanakdemir.noticeboard.data.model.Release
import net.furkanakdemir.noticeboard.data.model.ReleaseRaw
import net.furkanakdemir.noticeboard.data.repository.InMemoryNoticeBoardRepository
import net.furkanakdemir.noticeboard.data.repository.NoticeBoardRepository
import net.furkanakdemir.noticeboard.result.Result
import net.furkanakdemir.noticeboard.util.SingletonHolder
import net.furkanakdemir.noticeboard.util.color.ColorProvider
import net.furkanakdemir.noticeboard.util.color.NoticeBoardColorProvider
import net.furkanakdemir.noticeboard.util.io.DefaultFileReader
import net.furkanakdemir.noticeboard.util.io.FileReader
import net.furkanakdemir.noticeboard.util.mapper.ListMapper
import net.furkanakdemir.noticeboard.util.mapper.Mapper
import net.furkanakdemir.noticeboard.util.mapper.RealListMapper

internal class InternalNoticeBoard private constructor(context: Context?) {

    private var noticeBoardRepository: NoticeBoardRepository
    private var configRepository: ConfigRepository

    private val defaultColorProvider: ColorProvider = NoticeBoardColorProvider(context)

    init {
        requireNotNull(context) { "Context cannot be null" }
        val factory = buildDataSourceFactory(context)

        noticeBoardRepository = InMemoryNoticeBoardRepository(factory)
        configRepository = NoticeBoardConfigRepository(defaultColorProvider)
    }

    private fun buildDataSourceFactory(context: Context): NoticeBoardDataSourceFactory {
        val releaseListDomainMapper = buildMapper()
        val fileReader: FileReader = DefaultFileReader(context)
        return NoticeBoardDataSourceFactory(fileReader, releaseListDomainMapper)
    }

    private fun buildMapper(): ListMapper<ReleaseRaw, Release> {
        val changeDomainMapper: Mapper<ReleaseRaw.ChangeRaw, Release.Change> = ChangeDomainMapper()
        val changeListDomainMapper: ListMapper<ReleaseRaw.ChangeRaw, Release.Change> =
            RealListMapper(changeDomainMapper)
        val releaseDomainMapper: Mapper<ReleaseRaw, Release> =
            ReleaseDomainMapper(changeListDomainMapper)
        return RealListMapper(releaseDomainMapper)
    }

    fun saveColorProvider(colorProvider: ColorProvider) =
        configRepository.saveColorProvider(colorProvider)

    fun fetchChanges(sourceType: Source) = noticeBoardRepository.fetchChanges(sourceType)

    fun getColorProvider(): ColorProvider = configRepository.getColorProvider()

    fun getChanges(): Result<List<Release>> = noticeBoardRepository.getChanges()

    fun setDefaultColorProvider() = configRepository.saveColorProvider(defaultColorProvider)

    companion object : SingletonHolder<InternalNoticeBoard, Context>(::InternalNoticeBoard)
}
