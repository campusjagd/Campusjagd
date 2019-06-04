from flask import render_template
from app import app
from app.models import User, Challenge
from app import models, db

class ChallengeItem:
    title = ""
    official = ""
    rooms = ""
    points = ""

@app.route('/')
@app.route('/index')
def index():
    return render_template('index.html')

@app.route('/highscores')
def highscores():
    scorelist = db.session.query(User).order_by(User.points.desc())
    return render_template('highscores.html', scorelist = scorelist)


@app.route('/challenges')
def challenges():
    challengelist = []
    for c in db.session.query(Challenge).all():
        challenge_item = ChallengeItem()
        challenge_item.title = c.name
        challenge_item.official = c.official
        for r in c.rooms:
            challenge_item.rooms += r.name
            challenge_item.points += str(r.points)
        challengelist.append(challenge_item)
    return render_template('challenges.html', challengelist = challengelist)
