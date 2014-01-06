smtplistener
============

An email catcher.

Note that this requires configuring your MTA, so is nto trivial.
Exim in particular is very fussy.

DO NOT DEPLOY ON A MACHINE RESPONSIBLE FRO REAL MAIL.


Configuration
=============

in /etc/hosts add:

    127.0.0.1       smtplistener

Postfix
-------
in /etc/postfix/main.cf add:

    transport_maps = hash:/etc/postfix/transport

in /etc/postfix/transport add:

    smtplistener smtp:localhost:1616


then:

    cd /etc/postfix
    postmap /etc/postfix/transport
    /etc/init.d/postfix restart


Exim
----
in /etc/exim4/exim4.conf add in appropriate sections:

    domainlist relay_to_domains = smtplistener

    send_to_smtplistener:
       driver = manualroute
       route_list = smtplistener localhost
       transport = smtplistener_transport
       self = send

    smtplistener_transport:
      driver = smtp
      port = 1616


