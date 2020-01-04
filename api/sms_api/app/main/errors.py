from flask import jsonify

from . import main


@main.app_errorhandler(404)
def not_found(e):
    return jsonify(error=str(e), status_code=404), 404


@main.app_errorhandler(500)
def internal_server_error(e):
    return jsonify(error=str(e), status_code=500), 500
