import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.dv8tion.jda.api.components.MessageTopLevelComponent
import net.dv8tion.jda.api.components.container.ContainerChildComponent

@Serializable
sealed class Component {

    @Serializable
    @SerialName("10")
    data class TextDisplay(
        val content: String
    ) : Component() {
        override fun toDiscord() = dev.minn.jda.ktx.interactions.components.TextDisplay(content)

    }

    @Serializable
    @SerialName("17")
    data class Container(
        val accentColor: Int, val spoiler: Boolean, val mycomponents: List<Component>
    ) : Component() {
        override fun toDiscord(): MessageTopLevelComponent {
            return dev.minn.jda.ktx.interactions.components.Container(null, accentColor, spoiler) {
                this.components =
                    arrayListOf(*mycomponents.mapNotNull { it.toDiscord() as? ContainerChildComponent }.toTypedArray())
            }
        }
    }

    @Serializable
    @SerialName("14")
    data class Separator(
        val divider: Boolean, val spacing: Int
    ) : Component() {
        override fun toDiscord(): MessageTopLevelComponent {
            return dev.minn.jda.ktx.interactions.components.Separator(divider, net.dv8tion.jda.api.components.separator.Separator.Spacing.fromKey(spacing))
        }
    }

    abstract fun toDiscord(): MessageTopLevelComponent

    fun TextDisplay.toComponent() = TextDisplay(content)
    fun Container.toComponent() = Container(accentColor, spoiler, mycomponents.toList())
    fun Separator.toComponent() = Separator(divider, spacing)
}