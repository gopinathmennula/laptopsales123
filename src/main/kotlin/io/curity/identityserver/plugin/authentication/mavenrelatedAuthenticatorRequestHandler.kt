package io.curity.identityserver.plugin.authentication

import io.curity.identityserver.plugin.config.mavenrelatedAuthenticatorPluginConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se.curity.identityserver.sdk.attribute.Attribute
import se.curity.identityserver.sdk.authentication.AuthenticationResult
import se.curity.identityserver.sdk.authentication.AuthenticatorRequestHandler
import se.curity.identityserver.sdk.errors.ErrorCode
import se.curity.identityserver.sdk.http.RedirectStatusCode
import se.curity.identityserver.sdk.service.ExceptionFactory
import se.curity.identityserver.sdk.service.authentication.AuthenticatorInformationProvider
import se.curity.identityserver.sdk.web.Request
import se.curity.identityserver.sdk.web.Response

import java.net.MalformedURLException
import java.net.URL
import java.util.Optional
import java.util.UUID

import io.curity.identityserver.plugin.descriptor.mavenrelatedAuthenticatorPluginDescriptor.Companion.CALLBACK

class mavenrelatedAuthenticatorRequestHandler(private val _config: mavenrelatedAuthenticatorPluginConfig): AuthenticatorRequestHandler<Request>
{
    private val _authenticatorInformationProvider: AuthenticatorInformationProvider = _config.getAuthenticatorInformationProvider()
    private val _exceptionFactory: ExceptionFactory = _config.getExceptionFactory()

    override fun get(request: Request, response: Response): Optional<AuthenticationResult>
    {
        _logger.debug("GET request received for authentication")

        val redirectUri = createRedirectUri()
        val state = UUID.randomUUID().toString()
        val scopes = setOf("scope")

        _config.getSessionManager().put(Attribute.of("state", state))

        val queryStringArguments = linkedMapOf<String, Collection<String>>(
                "client_id" to setOf(_config.getClientId()),
                "redirect_uri" to setOf(redirectUri),
                "state" to setOf(state),
                "response_type" to setOf("code"),
                "scope" to setOf(scopes.joinToString(" "))
        )

        _logger.debug("Redirecting to {} with query string arguments {}", AUTHORIZATION_ENDPOINT,
                queryStringArguments)

        throw _exceptionFactory.redirectException(AUTHORIZATION_ENDPOINT,
                RedirectStatusCode.MOVED_TEMPORARILY, queryStringArguments, false)
    }

    private fun createRedirectUri(): String
    {
        try
        {
            val authUri = _authenticatorInformationProvider.fullyQualifiedAuthenticationUri

            return URL(authUri.toURL(), "${authUri.path}/$CALLBACK").toString()
        }
        catch (e: MalformedURLException)
        {
            throw _exceptionFactory.internalServerException(ErrorCode.INVALID_REDIRECT_URI,
                    "Could not create redirect URI")
        }
    }

    override fun post(request: Request, response: Response): Optional<AuthenticationResult>
    {
        throw _exceptionFactory.methodNotAllowed()
    }

    override fun preProcess(request: Request, response: Response): Request
    {
        return request
    }

    companion object
    {
        private val _logger: Logger = LoggerFactory.getLogger(mavenrelatedAuthenticatorRequestHandler::class.java)
        private const val AUTHORIZATION_ENDPOINT = ""

    }
}
