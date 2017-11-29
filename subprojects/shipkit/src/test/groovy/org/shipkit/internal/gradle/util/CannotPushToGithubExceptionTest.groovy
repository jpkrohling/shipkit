package org.shipkit.internal.gradle.util

import org.gradle.api.GradleException
import org.shipkit.gradle.git.GitPushTask
import org.shipkit.internal.gradle.util.handler.GitPushTaskExceptionHandler
import spock.lang.Specification
import spock.lang.Unroll

class GitPushTaskExceptionHandlerTest extends Specification {

    def "should throw proper message for lack of GH_WRITE_TOKEN"() {
        given:
            GitPushTask gitPushTask = Mock(GitPushTask)
            GradleException originalException = new GradleException("Exception message", new Throwable("original cause"))
            GitPushTaskExceptionHandler gitPushTaskExceptionHandler = new GitPushTaskExceptionHandler(gitPushTask)
        when:
            def result = gitPushTaskExceptionHandler.create(originalException)

        then:
            result.message == GitPushTaskExceptionHandler.GH_WRITE_TOKEN_NOT_SET_MSG
            result.cause.cause == originalException.getCause()
    }

    def "should throw proper message for invalid of GH_WRITE_TOKEN"() {
        given:
            GitPushTask gitPushTask = Mock(GitPushTask)
            GitPushTaskExceptionHandler gitPushTaskExceptionHandler = new GitPushTaskExceptionHandler(gitPushTask)
            gitPushTask.getSecretValue() >> "fake-token"
            GradleException originalException = new GradleException("Exception message", new Throwable("original cause"))
        when:
            def result = gitPushTaskExceptionHandler.create(originalException)
        then:
            result.message == GitPushTaskExceptionHandler.GH_WRITE_TOKEN_INVALID_MSG
            result.cause.cause == originalException.getCause()
    }

    @Unroll
    def "should match exception: #shouldMatch for message '#message'"() {
        given:
            GradleException ex = Mock(GradleException)
            ex.getMessage() >> message

        expect:
            GitPushTaskExceptionHandler gitPushTaskExceptionHandler = new GitPushTaskExceptionHandler(Mock(GitPushTask))
            gitPushTaskExceptionHandler.matchException(ex) == shouldMatch

        where:
            message | shouldMatch
            "sth Authentication failed sth" | true
            "sth unable to access sth"      | true
            "other message"                 | false
    }

}
