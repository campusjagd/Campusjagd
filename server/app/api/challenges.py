from app.api import bp
from flask import url_for, jsonify
from app import db
from app.models import Challenge
from app.api.errors import bad_request

@bp.route('/challenges', methods=['GET'])
def get_challenges():
    pass

@bp.route('/challenges', methods=['POST'])
def create_challenge():
    pass
