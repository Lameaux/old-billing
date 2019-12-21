from flask import request
from flask_restful import Resource
import time


class SmsRequests(Resource):
    def get(self):
        return [], 200

    def post(self):
        posted_data = request.get_json()
        errors = self._validate_sms_request(posted_data)
        if errors:
            return self._bad_request_response(errors)

        sms_request_id = int(time.time())

        return self._created_response(sms_request_id)

    @staticmethod
    def _validate_sms_request(sms_request):
        errors = {}

        if not 'msisdn' in sms_request:
            errors['msisdn'] = 'required'

        if not 'text' in sms_request:
            errors['text'] = 'required'

        return errors

    @staticmethod
    def _bad_request_response(errors):
        return dict(errors=errors, message='Bad Request'), 400


    @staticmethod
    def _created_response(id):
        return {"id": id}, 201
