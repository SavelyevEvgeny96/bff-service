package automation.validation

import automation.config.AllowedHost
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AllowedUrlValidatorTest {
    private val withSubs=AllowedUrlValidator(listOf(AllowedHost("example.com",true)))
    @ParameterizedTest @ValueSource(strings=["https://example.com","https://www.example.com","https://sub.example.com","HTTPS://EXAMPLE.COM/path","https://example.com?q=x","https://example.com/#fragment","https://example.com:8443/path"])
    fun `allows valid URLs`(url:String){ assertThat(withSubs.isAllowed(url)).isTrue() }
    @ParameterizedTest @ValueSource(strings=["https://example.com.attacker.test","https://example.com@attacker.test","javascript:alert(1)","file:///C:/Windows/System32","data:text/html,test","ftp://example.com","not a url",""])
    fun `denies unsafe URLs`(url:String){ assertThat(withSubs.isAllowed(url)).isFalse(); assertThatThrownBy{withSubs.requireAllowed(url)}.isInstanceOf(IllegalArgumentException::class.java) }
    @Test fun `subdomains depend on flag`(){ val exact=AllowedUrlValidator(listOf(AllowedHost("example.com",false))); assertThat(exact.isAllowed("https://example.com")).isTrue(); assertThat(exact.isAllowed("https://www.example.com")).isFalse() }
    @Test fun `enforces configured ports`(){ val v=AllowedUrlValidator(listOf(AllowedHost("example.com",false,setOf(443,8443)))); assertThat(v.isAllowed("https://example.com:8443")).isTrue(); assertThat(v.isAllowed("https://example.com:8080")).isFalse() }
}
