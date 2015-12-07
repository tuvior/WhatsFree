import webapp2
import string
import json
import datetime
import math
from google.appengine.ext import ndb
from submission import Submission
from session import Session
from vote import Vote


def json_string(
    id,
    name,
    image,
    rating,
    ):

    json_string = {
        'id': id,
        'name': name,
        'image': image,
        'rating': rating,
        }
    return json_string


def json_error(status, reason):
    json_string = {'search': {'status': status, 'reason': reason}}
    return json_string


class Search(webapp2.RequestHandler):

    def get(self):
        cookie = self.request.get('cookie')

        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'

        if not cookie:
            error = json_error('failure', 'cookie')
            self.response.write(json.dumps(error))
        else:

            session = Session.query(Session.cookie == cookie).get()

            if not session:
                error = json_error('failure', 'session')
                self.response.write(error)
            else:

                name = self.request.get('name')

                if not name:
                    error = json_error('failure', 'name')
                    self.response.write(json.dumps(error))
                else:

                    submissions_number = 40
                    submissions = Submission.query(Submission.lowerName == name.lower()).fetch(submissions_number)

                    if not submissions:
                        error = json_error('failure', 'no result')
                        self.response.write(json.dumps(error))

                    else:
                        submissions_array = []
                        if(submissions_number <= len(submissions)):
                            for i in range(0, submissions_number):
                                submission = submissions[i]
                                json_submission = json_string(submission.key.urlsafe(), submission.name, submission.image, submission.rating)
                                submissions_array.append(json_submission)

                        else:
                            for i in range(0, len(submissions)):
                                submission = submissions[i]
                                json_submission = json_string(submission.key.urlsafe(), submission.name, submission.image, submission.rating)
                                submissions_array.append(json_submission)


                        response = json.dumps(submissions_array)
                        self.response.write(response)

app = webapp2.WSGIApplication([('/search', Search)], debug=True)


			