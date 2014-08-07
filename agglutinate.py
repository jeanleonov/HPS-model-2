'''
@author: Anton
'''

from os import listdir
from os.path import isfile, join
import sys

def process_folder(folder_path):
    short_statistic_files = [ join(folder_path,f) for f in listdir(folder_path) if is_short_statistic_file(join(folder_path,f)) ]
    with file(folder_path+'/result.csv','w') as result_file:
        file_processor = file_processor_generator(result_file)
        file_processor.next()
        for file_path in short_statistic_files:
            file_processor.send(file_path)

def is_short_statistic_file(path):
    return isfile(path) and path.endswith('.csv') and not path.endswith('result.csv')

def file_processor_generator(result_file):
    should_copy_headers = True
    while True:
        file_path = yield
        with file(file_path,'r') as csv_file:
            file_content = csv_file.read()
            if should_copy_headers: lines = file_content.split('\n');    should_copy_headers=False
            else:                   lines = file_content.split('\n')[2:]
            for line in lines:
                if len(line)>0:
                    result_file.write(line+'\n')
 
if __name__ == '__main__':
    process_folder(sys.argv[1])