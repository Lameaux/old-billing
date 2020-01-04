FROM python:3.7

ENV FLASK_APP mock_server.py
ENV FLASK_CONFIG production

WORKDIR /usr/src/app
COPY requirements.txt .
RUN pip install --upgrade pip --no-cache-dir -r requirements.txt
COPY . .
CMD ["./boot.sh"]
