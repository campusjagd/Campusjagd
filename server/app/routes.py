from flask import render_template
from app import app
from app.models import User, Challenge
from app import models, db


@app.route('/')
@app.route('/index')
def index():
    return render_template('index.html')

@app.route('/highscores')
def highscores():
    scorelist = db.session.query(User).order_by(User.points.desc())
    return render_template('highscores.html', scorelist = scorelist)


