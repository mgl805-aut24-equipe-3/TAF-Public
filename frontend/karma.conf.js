// Karma configuration file, see link for more information
// https://karma-runner.github.io/1.0/config/configuration-file.html

module.exports = function (config) {
  // Check if the CI environment variable is set (GitHub Actions and most CI tools set this automatically)
  const isCI = process.env.CI;

  config.set({
    basePath: '',
    frameworks: ['jasmine', '@angular-devkit/build-angular'],
    files: [
      'src/**/*.spec.js', // Include all test files in the src directory
    ],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-jasmine-html-reporter'),
      require('karma-coverage'),
      require('@angular-devkit/build-angular/plugins/karma')
    ],
    client: {
      jasmine: {
        // you can add configuration options for Jasmine here
        // the possible options are listed at https://jasmine.github.io/api/edge/Configuration.html
        // for example, you can disable the random execution with `random: false`
        // or set a specific seed with `seed: 4321`
      },
      clearContext: false // leave Jasmine Spec Runner output visible in browser
    },
    jasmineHtmlReporter: {
      suppressAll: true // removes the duplicated traces
    },
    coverageReporter: {
      dir: require('path').join(__dirname, './coverage/taf-ui'),
      subdir: '.',
      reporters: [
        { type: 'html' },
        { type: 'text-summary' }
      ],
      check: { //The check property causes the tool to enforce a minimum of 80% code coverage when the unit tests are run in the project.
        global: {
          statements: 40,
          branches: 40,
          functions: 40,
          lines: 40,
          includes: ['src/**/performance-test-api/**/*.ts'], 
        },
      }
    },
    reporters: ['progress', 'kjhtml'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: true,
    browsers: [isCI ? 'ChromeHeadless': 'Chrome'],
    singleRun: isCI, // On CI, ensure the runner exits in CI after tests complete
    restartOnFileChange: true
  });
};
