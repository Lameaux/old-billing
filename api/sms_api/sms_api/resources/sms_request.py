from flask_restful import Resource


class SmsRequest(Resource):
    def get(self, id):
        return self._ok_response(id)

    def put(self, id):
        return self._ok_response(id)

    def delete(self, id):
        return self._ok_response(id)

    @staticmethod
    def _ok_response(id):
        return {"id": id}, 200