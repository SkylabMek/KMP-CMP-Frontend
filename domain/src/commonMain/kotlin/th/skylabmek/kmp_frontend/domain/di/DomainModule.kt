package th.skylabmek.kmp_frontend.domain.di

import org.koin.dsl.module
import th.skylabmek.kmp_frontend.core.network.di.NetworkQualifier
import th.skylabmek.kmp_frontend.domain.data.repository.auth.AuthRepositoryImpl
import th.skylabmek.kmp_frontend.domain.data.repository.feature.FeatureRepositoryImpl
import th.skylabmek.kmp_frontend.domain.data.repository.performances.PerformanceRepositoryImpl
import th.skylabmek.kmp_frontend.domain.data.repository.profile.ProfileRepositoryImpl
import th.skylabmek.kmp_frontend.domain.repository.auth.AuthRepository
import th.skylabmek.kmp_frontend.domain.repository.feature.FeatureRepository
import th.skylabmek.kmp_frontend.domain.repository.performances.PerformanceRepository
import th.skylabmek.kmp_frontend.domain.repository.profile.ProfileRepository

val domainModuleModules = module {
    single<FeatureRepository> {
        FeatureRepositoryImpl(get(qualifier = NetworkQualifier.Default.qualifier))
    }
    single<ProfileRepository> {
        ProfileRepositoryImpl(get(qualifier = NetworkQualifier.Default.qualifier))
    }
    single<PerformanceRepository> {
        PerformanceRepositoryImpl(get(qualifier = NetworkQualifier.Default.qualifier))
    }
    single<AuthRepository> {
        AuthRepositoryImpl(
            get(qualifier = NetworkQualifier.Default.qualifier),
            get()
        )
    }
}
