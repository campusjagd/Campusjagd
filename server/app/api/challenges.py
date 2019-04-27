from app.api import bp

@bp.route('/challenges', methods=['GET'])
def get_challenges():
    pass

@bp.route('/challenges', methods=['POST'])
def create_challenge():
    pass
