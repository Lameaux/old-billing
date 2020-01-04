from flask import Blueprint

sms_requests = Blueprint('sms_requests', __name__)

from . import endpoints
