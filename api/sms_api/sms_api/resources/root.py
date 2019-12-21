from flask_restful import Resource


class Root(Resource):
    def get(self):
        return 'SMS API v1'

