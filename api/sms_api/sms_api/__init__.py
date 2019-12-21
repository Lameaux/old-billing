from flask import Flask, Blueprint
from flask_restful import Api
from flask_pymongo import PyMongo

from sms_api.resources.root import Root
from sms_api.resources.sms_request import SmsRequest
from sms_api.resources.sms_requests import SmsRequests

db = PyMongo()


def create_app():
    app = Flask(__name__, instance_relative_config=False)
    app.config.from_object('config.Config')

    db.init_app(app)

    api_bp = Blueprint('api', __name__)
    api = Api(api_bp)

    api.add_resource(Root, '/')
    api.add_resource(SmsRequests, '/v1/sms/requests', '/v1/sms/send')
    api.add_resource(SmsRequest, '/v1/sms/requests/<string:id>')

    app.register_blueprint(api_bp)
    return app
