// Protractor configuration file, see link for more information
// https://github.com/angular/protractor/blob/master/lib/config.ts

const {SpecReporter} = require('jasmine-spec-reporter');
const jasmineReporters = require('jasmine-reporters');

exports.config = {
  allScriptsTimeout: 11000,
  specs: [
    './e2e/**/*.e2e-spec.ts'
  ],
  multiCapabilities: [
    {
      'browserName': 'chrome'
    },
    {
      'browserName': 'firefox'
    }
  ],
  seleniumAddress: 'http://browsers.int.jsherz.com:4444/wd/hub',
  directConnect: false,
  baseUrl: 'https://dev.leedsskydivers.com/',
  framework: 'jasmine',
  jasmineNodeOpts: {
    showColors: true,
    defaultTimeoutInterval: 30000,
    print: function () {
    }
  },
  onPrepare() {
    jasmine.getEnv().addReporter(new SpecReporter({spec: {displayStacktrace: true}}));
    jasmine.getEnv().addReporter(new jasmineReporters.JUnitXmlReporter({
      consolidateAll: true,
      savePath: 'testresults',
      filePrefix: 'reportXMLoutput'
    }));

    require('ts-node').register({
      project: 'e2e/tsconfig.e2e.json'
    });
  }
};
