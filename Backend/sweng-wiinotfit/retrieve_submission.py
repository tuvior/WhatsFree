import webapp2
import string
import json
import datetime
import math
from google.appengine.ext import ndb
from google.appengine.api import images
from submission import Submission
from session import Session
from vote import Vote


def json_string(id, name, category , description , location , image , keywords, submitter, tfrom, tto, rating, latitude, longitude, vote):
    json_string = {'id': id, 'name': name,'category': category, 'description': description, 'location': location, 'image': image,
                   'keywords': keywords, 'submitter': submitter, 'from': tfrom, 'to': tto, 'rating': rating, 'latitude': latitude,
                   'longitude': longitude, 'vote': vote}
    return json_string

def json_array(id, name, image, rating):
    json_string = {'id': id, 'name': name, 'image': image, 'rating': rating, 'location': location}
    return json_string

def json_error(option, status, reason):
    json_string = {option: {'status': status, 'reason': reason}}
    return json_string

class retrieveSubmission(webapp2.RequestHandler):
    def get(self):
        cookie = self.request.get('cookie')

        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'

        if not cookie:
            error = json_error('retrieve', 'failure', 'cookie')
            self.response.write(json.dumps(error))

        else:
            flag = self.request.get('flag')
        
            if not flag:
                error = json_error('retrieve', 'failure', 'no flag')
                self.response.write(json.dumps(error))

            else:
                session = Session.query(Session.cookie == cookie).get()

                if not session:
                    error = json_error('retrieve', 'failure', 'session')
                    self.response.write(error)

                else:
                    # flag = 1 means a single request
                    if flag == '1':
                        try:
                            id = self.request.get('id');
                            key = ndb.Key(urlsafe=id)

                            if not id:
                                self.response.write(json.dumps(json_error('single request', 'failure', 'id')))

                            else:
                                submission = key.get()

                                if not submission:
                                    error = json_error('single request', 'failure', 'no corresponding submission')
                                    self.response.write(json.dumps(error))

                                else:
                                    last_vote = Vote.query(ndb.AND(Vote.user == session.user, Vote.sub_id == submission.key.urlsafe())).get()
                                    if not last_vote:
                                        vote_val = 0     
                                    else:
                                        vote_val = last_vote.value
    
                                    string_submission = json_string(submission.key.urlsafe(), submission.name, submission.category, submission.description, submission.location,
                                                                    submission.image, submission.keywords, submission.submitter,
                                                                    submission.tfrom, submission.tto, submission.rating, submission.latitude,
                                                                    submission.longitude, vote_val)
                                    response = json.dumps(string_submission)
                                    self.response.write(response)

                        except:
                            self.response.write(json.dumps(json_error('single request', 'failure', 'id')))


                    # flag = 2 means that we are requesting submissions to display in what's new
                    elif flag == '2':
                        time_range = 1447786800
                        date = datetime.datetime.fromtimestamp(time_range/1e3)
                        submissions_number = 20

                        submissions = Submission.query(Submission.submitted >= date).fetch(submissions_number)

                        if not submissions:
                            error = json_error('what is new', 'failure', 'nothing in range')
                            self.response.write(json.dumps(error))

                        else:
                            submissions_array = []
                            if(submissions_number <= len(submissions)):
                                for i in range(0, submissions_number):
                                    submission = submissions[i]
                                    # what's new used for around you too. Server return location so that the app
                                    # can use it to display submissions near user on the map
                                    json_submission = json_array(submission.key.urlsafe(), submission.name,submission.image, submission.rating, submission.location)
                                    submissions_array.append(json_submission)

                            else:
                                for i in range(0, len(submissions)):
                                    submission = submissions[i]
                                    json_submission = json_array(submission.key.urlsafe(), submission.name, submission.image, submission.rating, submission.location)
                                    submissions_array.append(json_submission)


                            response = json.dumps(submissions_array)
                            self.response.write(response)

                    # flag = 4 means that we are requesting submissions for a specific category
                    elif flag == '4':
                        category = self.request.get('category')
                        submissions_number = 20

                        if not category:
                            error = json_error('retrieve category', 'failure', 'no category')
                            self.response.write(json.dumps(error))

                        else:
                            submissions = Submission.query(Submission.category == category).fetch(submissions_number)

                            if not submissions:
                                error = json_error('retrieve category', 'failure', 'empty category')
                                self.response.write(json.dumps(error))

                            else:
                                submissions_array = []
                                if(submissions_number <= len(submissions)):
                                    for i in range(0, submissions_number):
                                        submission = submissions[i]
                                        json_submission = json_array(submission.key.urlsafe(), submission.name,submission.image, submission.rating)
                                        submissions_array.append(json_submission)

                                else:
                                    for i in range(0, len(submissions)):
                                        submission = submissions[i]
                                        json_submission = json_array(submission.key.urlsafe(), submission.name,submission.image, submission.rating)
                                        submissions_array.append(json_submission)

                                response = json.dumps(submissions_array)
                                self.response.write(response)

                    # every other flag generate an error
                    else:
                        error = json_error('retrieve' ,'failure', 'flag')
                        self.response.write(json.dumps(error))


app = webapp2.WSGIApplication([
('/retrieve', retrieveSubmission),
], debug=True)