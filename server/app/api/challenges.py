from app.api import bp
from flask import url_for, jsonify
from app import db
from app.models import Challenge
from app.api.errors import bad_request

@bp.route('/challenges', methods=['GET'])
def get_challenges():
    data = {
        "get_challenges": [challenge.to_dict() for challenge in db.session.query(Challenge).all()],
        "number_of_challenges": db.session.query(Challenge).count()
    }
    return jsonify(data)

@bp.route('/challenges', methods=['POST'])
def create_challenge():
    data = request.get_json() or {}
    if 'name' not in data:
        return bad_request('must include challengename')
    if db.session.query(Challenge).filter_by(name=data['name'].upper()).first():
        return bad_request('challenge aready existing')
    else:
        # add challenge
        challenge = Challenge()
        challenge.name = data['name']
        # challenge.rooms
        db.session.add(challenge)
        db.session.commit()
    response = jsonify(challenge.to_dict())
    response.status_code = 201
    response.headers['Location'] = url_for('api.create_challenge', id=challenge.id)
    return response
