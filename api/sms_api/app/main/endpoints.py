from flask import render_template, current_app

from . import main


@main.route('/')
def index():
    return 'SMS API'


@main.route('/health')
def health():
    return 'OK'
