from app.api import bp

@bp.route('/rooms', methods=['GET'])
def get_rooms():
    pass

@bp.route('/rooms', methods=['POST'])
def create_room():
    pass
