package com.impact.analyser.configure;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.impact.analyser.impl.TestMapper;
import com.impact.analyser.impl.TestMapperUpdated;
import com.impact.analyser.interfaces.ITestMapper;
import com.impact.analyser.utils.ClassUtils;
import com.impact.analyser.impl.RetrievePageInformation;
import com.impact.analyser.impl.RetrieveTestInformation;
import com.impact.analyser.interfaces.IPageInformation;
import com.impact.analyser.interfaces.ITestDefInformation;

/**
 * Created by Yuvaraj on 14/03/2018.
 */
public class ImpactAnalyserConfiguration extends AbstractModule {

    @Override
    public void configure() {
        bind(ITestDefInformation.class).to(RetrieveTestInformation.class);
        bind(IPageInformation.class).to(RetrievePageInformation.class);
        bind(ITestMapper.class).to(TestMapperUpdated.class);
        requestStaticInjection(ClassUtils.class);
        bindListener(Matchers.any(), new LoggingListner());
    }
}
