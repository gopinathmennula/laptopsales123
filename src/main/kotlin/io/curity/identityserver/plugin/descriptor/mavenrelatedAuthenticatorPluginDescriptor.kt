package io.curity.identityserver.plugin.descriptor

import io.curity.identityserver.plugin.authentication.CallbackRequestHandler
import io.curity.identityserver.plugin.authentication.mavenrelatedAuthenticatorRequestHandler
import io.curity.identityserver.plugin.config.mavenrelatedAuthenticatorPluginConfig
import se.curity.identityserver.sdk.plugin.descriptor.AuthenticatorPluginDescriptor

class mavenrelatedAuthenticatorPluginDescriptor: AuthenticatorPluginDescriptor<mavenrelatedAuthenticatorPluginConfig>
{

    override fun getPluginImplementationType() = "mavenrelated"

    override fun getConfigurationType() = mavenrelatedAuthenticatorPluginConfig::class.java

    override fun getAuthenticationRequestHandlerTypes() = linkedMapOf(
        "index" to mavenrelatedAuthenticatorRequestHandler::class.java,
        CALLBACK to CallbackRequestHandler::class.java
    )

    companion object
    {
        const val CALLBACK: String = "callback"
    }
}
