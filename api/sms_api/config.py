import os
from dotenv import load_dotenv
load_dotenv()


class Config:
    DEBUG = os.getenv('DEBUG')
    SECRET_KEY = os.environ.get('SECRET_KEY')
    MONGO_URI = os.environ.get('MONGO_URI')
