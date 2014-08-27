
Faux Pas Jenkins Plugin
=======================

This is a plugin for running [Faux Pas] in the [Jenkins] continuous integration server.

[Faux Pas]: http://fauxpasapp.com
[Jenkins]: http://jenkins-ci.org


Installation
------------

This plugin is not yet available in the standard Jenkins repositories, so you must download the `.hpi` file (from the `dist/` folder in this repository) and install it manually via _Manage Jenkins > Manage Plugins > Advanced > Upload Plugin_ in the Jenkins GUI.


Usage
-----

1. Open the configuration page for the desired job
2. Add build step “Faux Pas”
3. Configure the project and Faux Pas config file paths for the build step _(it is recommended to have a separate Faux Pas configuration file in your project repository for CI — refer to this file here)_
4. Add post-build action “Publish Faux Pas checking results”

It is recommended to configure the `minErrorStatusSeverity` option in the Faux Pas configuration file — this allows you to control when Faux Pas returns a nonzero exit status (which will break the CI build).



Building
--------

1. Run `mvn package`
2. The built plugin can be found at: `target/fauxpas-plugin.hpi`

