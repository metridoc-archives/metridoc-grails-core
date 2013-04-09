package metridoc.core

import org.junit.Test

/**
 * Created with IntelliJ IDEA on 4/9/13
 *
 * @author Tommy Barker
 *
 */
class JobWrapperTest {

    def wrapper = new JobWrapper()

    @Test
    void "jobHasBinding returns true if the job has a binding"() {
        testWrappedJobHasBinding(new HasBinding(), true)
    }

    @Test
    void "jobHasBinding returns false if job does not have binding"() {
        testWrappedJobHasBinding(new DoesNotHaveBinding(), false)
    }

    @Test
    void "jobHasBinding returns false if job has binding, but is wrong type"() {
        testWrappedJobHasBinding(new HasBindingButWrongType(), false)
    }

    @Test
    void "test interrupt method detection"() {
        wrapper.job = new HasInterruptedAndBinding()
        assert wrapper.jobHasInterruptMethod()
        wrapper.job = new HasBinding()
        assert !wrapper.jobHasInterruptMethod()
    }

    @Test
    void "if interrupted, the job is interrupted if it has an interrupt method and interrupted is set in the binding"() {

        def job = new HasInterruptedAndBinding()
        wrapper.job = job
        wrapper.interrupt()
        assert job.binding.hasVariable(JobWrapper.INTERRUPT)
        assert job.interruptCalled
    }

    private void testWrappedJobHasBinding(job, boolean condition) {
        wrapper.job = job
        assert condition == wrapper.jobHasBinding()
    }
}

class HasInterruptedAndBinding {
    Binding binding = new Binding()
    boolean interruptCalled = false

    void interrupt() {
        interruptCalled = true
    }

}

class HasBinding {
    Binding binding
}

class DoesNotHaveBinding {

}

class HasBindingButWrongType {
    String binding = "binding"
}
