package com.spring.batch.jpa.multi.fault.exceptionskip;

import org.springframework.batch.core.step.skip.NonSkippableReadException;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.validation.BindException;

public class ExceptionSkipPolicy  implements SkipPolicy{
	
	@Override
	public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
	
		return (t instanceof NumberFormatException) || (t instanceof NonSkippableReadException) ;
			
	}

}
