import webapp2
import string
import json
import datetime
from google.appengine.ext import ndb
from google.appengine.api import images
from submission import Submission
from session import Session

def json_response(status):
    if status == -1:
        res = """{
    "retrieve": {
        "status": "invalid"
    }
}"""    
    
    elif status == 1:
        res = """{
    "retrieve": {
        "status": "failure",
        "reason": "name"
    }
}"""

    return res

# check which arguments are there 
def json_string(name, category , description , location , image , keywords, submitter, tfrom, tto):
    json_string = {'name': name,'category': category, 'description': description, 'location': location, 'image': image,
                   'keywords': keywords, 'submitter': submitter, 'from': tfrom, 'to': tto}
    return json_string


def json_error(option, status, reason):
    json_string = {option: {'status': status, 'reason': reason}}
    return json_string

# add cookie
class retrieveSubmission(webapp2.RequestHandler):
    def get(self):
        cookie = self.request.get('cookie')

        self.response.headers['Content-Type'] = 'application/json; charset=utf-8'

        if not cookie:
            error = json_error('none', 'failure', 'cookie')
            self.response.write(json.dumps(error))

        else:
            flag = self.request.get('flag')
        
            if not flag:
                error = json_error('retrieve', 'failure', 'flag')
                self.response.write(json.dumps(error))

            else:
                session = Session.query(Session.cookie == cookie).get()

                if not session:
                    error = json_error('none', 'failure', 'session')
                    self.response.write(error)

                else:
                    # flag = 1 means a single request
                    if flag == '1':
                        name = self.request.get('name');

                        if not name:
                            self.response.write(json.dumps(json_error('single request', 'failure', 'name')))

                        else:
                            submission = Submission.query(Submission.name == name).get()

                            if not submission:
                                error = json_error('single request', 'failure', 'no corresponding submission')
                                self.response.write(json.dumps(error))

                            else:
                                string_submission = json_string(submission.name, submission.category, submission.description, submission.location,
                                                                submission.image, submission.keywords, submission.submitter,
                                                                submission.tfrom, submission.tto)
                                response = json.dumps(string_submission)
                                self.response.write(response)

                    # flag = 2 means that we are requesting submissions to display in what's new
                    elif flag == '2':
                        time_range = 1447786800
                        date = datetime.datetime.fromtimestamp(time_range/1e3)
                        submissions_number = 5

                        submissions = Submission.query(Submission.submitted >= date).fetch(submissions_number)

                        if not submissions:
                            error = json_error('what is new', 'failure', 'nothing in range')
                            self.response.write(json.dumps(error))

                        else:
                            submissions_array = []
                            if(submissions_number <= len(submissions)):
                                for i in range(0, submissions_number):
                                    submission = submissions[i]
                                    # use thumbnail
                                    json_submission = json_string(submission.name, '', '', '', submission.image, '', '', '', '')
                                    submissions_array.append(json_submission)

                            else:
                                for i in range(0, len(submissions)):
                                    submission = submissions[i]
                                    json_submission = json_string(submission.name, '', '', '', submission.image, '', '', '', '')
                                    submissions_array.append(json_submission)


                            response = json.dumps(submissions_array)
                            self.response.write(response)
        
                    # flag = 3 means that we are requesting submissions for around you

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
                                if(submissions_number <= len(submissions_array)):
                                    for i in range(0, submissions_number):
                                        submission = submissions[i]
                                        json_submission = json_string(submission.name, '', '', '', submission.image, '', '', '', '')
                                        submissions_array.append(json_submission)

                                        # Once BadImageError fixed do

                                        #image_to_resize = images.Image(submission.image)
                                        #image_to_resize.resize(width=90, height=90)
                                        #image_to_resize.im_feeling_lucky()

                                        #thumbnail = image_to_resize.execute_transforms(output_encoding=images.JPEG)

                                else:
                                    for i in range(0, len(submissions)):
                                        submission = submissions[i]
                                        json_submission = json_string(submission.name, '', '', '', submission.image, '', '', '', '')
                                        submissions_array.append(json_submission)

                                response = json.dumps(submissions_array)
                                self.response.write(response)

                    # flag = 5 means that we are using the search option
                    elif flag == '5':
                        name = self.request.get('name')

                        if not name:
                            error = json_error('search', 'failure', 'name')
                            self.response.write(json.dumps(error))

                        else:
                            submission = Submission.query(Submission.name == name).get()

                            if not submission:
                                error = json_error('search', 'failure', 'submission')
                                self.response.write(json.dumps(error))

                            else:
                                json_submission = json_string(submission.name, '', '', '', submission.image, '', '', '', '')
                                self.response.write(json.dumps(json_submission))

                    # every other flag generate an error
                    else:
                        error = json_error('no option' ,'failure', 'flag')
                        self.response.write(json.dumps(error))


app = webapp2.WSGIApplication([
('/retrieve', retrieveSubmission),
], debug=True)