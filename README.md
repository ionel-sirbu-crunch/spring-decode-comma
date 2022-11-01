This is a demo project for a Spring Boot issue where `GET` request parameters where if a value contains a comma (`,`)
it is not properly URL decoded during parameter binding. It is considered as element separator for collections when it
should be in fact part of the value of an element.