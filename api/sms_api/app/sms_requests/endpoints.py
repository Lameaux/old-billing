from flask import request, jsonify
from bson import json_util
import json

from . import sms_requests
from .. import mongo


def _validate_sms_request(sms_request):
    errors = {}

    if sms_request is None:
        errors['body'] = 'invalid payload'
        return errors

    if not 'msisdn' in sms_request:
        errors['msisdn'] = 'required'

    if not 'text' in sms_request:
        errors['text'] = 'required'

    return errors


def _bad_request_response(errors):
    return dict(errors=errors, message='Bad Request'), 400


def _bson_to_dict(bson):
    return json.loads(json_util.dumps(bson))


def _create_sms_request(json):
    return {
        'msisdn': json['msisdn'],
        'text': json['text'],
    }


@sms_requests.route('/', methods=['get'])
def show_all():
    sms_requests = _bson_to_dict(mongo.db.sms_requests.find())
    return jsonify(sms_requests)


@sms_requests.route('/', methods=['post'])
def post():
    posted_data = request.get_json()
    errors = _validate_sms_request(posted_data)
    if errors:
        return _bad_request_response(errors)

    sms_request = _create_sms_request(posted_data)
    sms_request = mongo.db.sms_requests.save(sms_request)

    return jsonify(_bson_to_dict(sms_request)), 201


@sms_requests.route('/<int:sms_request_id>', methods=['get'])
def get(sms_request_id):
    return jsonify({'id': sms_request_id})


@sms_requests.route('/<int:sms_request_id>', methods=['put'])
def put(sms_request_id):
    return jsonify({'id': sms_request_id})


@sms_requests.route('/<int:sms_request_id>', methods=['delete'])
def delete(sms_request_id):
    return jsonify({'id': sms_request_id})
