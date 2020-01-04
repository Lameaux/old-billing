from flask import Flask
from flask_pymongo import PyMongo

from config import config

mongo = PyMongo()


def create_app(config_name):
    app = Flask(__name__)
    app.config.from_object(config[config_name])
    config[config_name].init_app(app)

    mongo.init_app(app)

    if app.config['SSL_REDIRECT']:
        from flask_sslify import SSLify
        sslify = SSLify(app)

    from .main import main as main_blueprint
    app.register_blueprint(main_blueprint)

    from .sms_requests import sms_requests as sms_requests_blueprint
    app.register_blueprint(sms_requests_blueprint, url_prefix='/api/v1/sms/requests')

    return app
