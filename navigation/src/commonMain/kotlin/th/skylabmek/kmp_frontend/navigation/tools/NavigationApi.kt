package th.skylabmek.kmp_frontend.navigation.tools

import androidx.navigation3.runtime.EntryProviderScope
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import androidx.navigation3.runtime.NavKey as BaseNavKey

/**
 * Base interface for all navigation keys (routes).
 * Features should implement this and annotate their implementations with @Serializable.
 * We use a typealias to ensure compatibility with Navigation 3's internal APIs
 * while keeping a clean name within our project.
 */
typealias NavKey = BaseNavKey

/**
 * Interface that each feature must implement to provide its navigation entries
 * and serialization support.
 */
interface FeatureNavProvider {
    /**
     * DSL builder for navigation entries.
     * Features implement this to register their screens.
     */
    fun EntryProviderScope<NavKey>.navigationBuilder()

    /**
     * Registers the serializable subclasses of NavKey for this feature.
     */
    fun registerSerializers(polymorphicModuleBuilder: PolymorphicModuleBuilder<NavKey>) {}

    /**
     * Maps a deep link URI to a NavKey if this feature handles it.
     */
    fun mapUriToNavKey(uri: String): NavKey? = null

    /**
     * Maps a NavKey to a URI path.
     */
    fun mapNavKeyToUri(key: NavKey): String? = null
}
