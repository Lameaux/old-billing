import os


class Config:
    DEBUG = False
    TESTING = False
    SSL_REDIRECT = False
    MONGO_URI = os.environ.get('MONGO_URI')

    @staticmethod
    def init_app(app):
        pass


class DevelopmentConfig(Config):
    DEBUG = True
    MONGO_URI = 'mongodb://localhost:27017/sms_api_dev'


class TestingConfig(Config):
    TESTING = True
    MONGO_URI = 'mongodb://localhost:27017/sms_api_test'


class ProductionConfig(Config):
    @staticmethod
    def init_app(app):
        Config.init_app(app)

        # log to stderr
        import logging
        from logging import StreamHandler
        file_handler = StreamHandler()
        file_handler.setLevel(logging.INFO)
        app.logger.addHandler(file_handler)


class HerokuConfig(ProductionConfig):
    SSL_REDIRECT = True if os.environ.get('DYNO') else False

    @staticmethod
    def init_app(app):
        ProductionConfig.init_app(app)

        # handle reverse proxy server headers
        from werkzeug.middleware.proxy_fix import ProxyFix
        app.wsgi_app = ProxyFix(app.wsgi_app)


config = {
    'development': DevelopmentConfig,
    'testing': TestingConfig,
    'production': ProductionConfig,
    'heroku': HerokuConfig,

    'default': DevelopmentConfig
}