#!/bin/sh

exec gunicorn -b :5000 --access-logfile - --error-logfile - sms_api:app
